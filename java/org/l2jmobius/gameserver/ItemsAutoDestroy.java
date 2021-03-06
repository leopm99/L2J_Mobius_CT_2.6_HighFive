/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.l2jmobius.Config;
import org.l2jmobius.commons.concurrent.ThreadPool;
import org.l2jmobius.commons.util.Chronos;
import org.l2jmobius.gameserver.enums.ItemLocation;
import org.l2jmobius.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2jmobius.gameserver.model.items.instance.ItemInstance;

public class ItemsAutoDestroy
{
	private final List<ItemInstance> _items = new LinkedList<>();
	
	protected ItemsAutoDestroy()
	{
		ThreadPool.scheduleAtFixedRate(this::removeItems, 5000, 5000);
	}
	
	public static ItemsAutoDestroy getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	public synchronized void addItem(ItemInstance item)
	{
		item.setDropTime(Chronos.currentTimeMillis());
		_items.add(item);
	}
	
	private synchronized void removeItems()
	{
		if (_items.isEmpty())
		{
			return;
		}
		
		final long curtime = Chronos.currentTimeMillis();
		final Iterator<ItemInstance> itemIterator = _items.iterator();
		while (itemIterator.hasNext())
		{
			final ItemInstance item = itemIterator.next();
			if ((item.getDropTime() == 0) || (item.getItemLocation() != ItemLocation.VOID))
			{
				itemIterator.remove();
			}
			else
			{
				final long autoDestroyTime;
				if (item.getItem().getAutoDestroyTime() > 0)
				{
					autoDestroyTime = item.getItem().getAutoDestroyTime();
				}
				else if (item.getItem().hasExImmediateEffect())
				{
					autoDestroyTime = Config.HERB_AUTO_DESTROY_TIME;
				}
				else
				{
					autoDestroyTime = ((Config.AUTODESTROY_ITEM_AFTER == 0) ? 3600000 : Config.AUTODESTROY_ITEM_AFTER * 1000);
				}
				
				if ((curtime - item.getDropTime()) > autoDestroyTime)
				{
					item.decayMe();
					itemIterator.remove();
					if (Config.SAVE_DROPPED_ITEM)
					{
						ItemsOnGroundManager.getInstance().removeObject(item);
					}
				}
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final ItemsAutoDestroy INSTANCE = new ItemsAutoDestroy();
	}
}