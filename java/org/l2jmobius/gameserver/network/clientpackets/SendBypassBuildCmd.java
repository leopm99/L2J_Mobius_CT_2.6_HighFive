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
package org.l2jmobius.gameserver.network.clientpackets;

import org.l2jmobius.commons.network.PacketReader;
import org.l2jmobius.gameserver.handler.AdminCommandHandler;
import org.l2jmobius.gameserver.model.actor.instance.PlayerInstance;
import org.l2jmobius.gameserver.network.GameClient;

/**
 * This class handles all GM commands triggered by //command
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:29 $
 */
public class SendBypassBuildCmd implements IClientIncomingPacket
{
	public static final int GM_MESSAGE = 9;
	public static final int ANNOUNCEMENT = 10;
	
	private String _command;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_command = packet.readS();
		if (_command != null)
		{
			_command = _command.trim();
		}
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final PlayerInstance player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		AdminCommandHandler.getInstance().useAdminCommand(player, "admin_" + _command, true);
	}
}
