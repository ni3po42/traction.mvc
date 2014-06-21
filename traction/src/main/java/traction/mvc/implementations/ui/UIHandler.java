/* Copyright 2013 Tim Stratton

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package traction.mvc.implementations.ui;

import android.os.Handler;

/**
 * Simply a handler, with the added ability to execute a task immediately if it's executing on the same thread it was created.
 * The reason for this was a timing issue causing circular calls that brought the application to it's knees.Posting it would add
 * the taks to a queue and execute it when it got to it. you can add it to the front, but there was still a chance of execution 
 * happening after a cycle.
 * @author Tim Stratton
 *
 */
public class UIHandler extends Handler
{
	private Thread uiThread;
	public UIHandler()
	{
		//save an instance of the current thread, where it was created.
		uiThread = Thread.currentThread();
	}

	public void tryPostImmediatelyToUIThread(Runnable action)
	{
		//if the current thread is same as the one where handler was, just run it, otherwise, post it.
		//this is very similar to how the activity can runOnUiThread.
		 if (Thread.currentThread() != uiThread) {
	            post(action);
	        } else {
	            action.run();
	        }
	}
}
