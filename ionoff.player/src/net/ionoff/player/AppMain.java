/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2015 Caprica Software Limited.
 */

package net.ionoff.player;

import net.ionoff.player.config.AppConfig;
import net.ionoff.player.config.AppUtil;
import net.ionoff.player.handler.TcpConnection;
import net.ionoff.player.thread.InformStatusThread;
import net.ionoff.player.thread.LatestVersionDownloader;

/**
 * Application entry-point.
 */
public class AppMain {

	public static void main(String[] args) throws InterruptedException {
		System.setProperty("app.dir", AppUtil.getCurrentDir());
		long heapSize = Runtime.getRuntime().totalMemory();
		System.out.println("Heap Size = " + heapSize);
		new LatestVersionDownloader().start();
		if (AppConfig.getInstance().INTERVAL_UPDATE) {
			new InformStatusThread().start();
		}
		new TcpConnection().start();
	}
}
