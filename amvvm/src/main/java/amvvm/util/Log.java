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

package amvvm.util;


/**
 * Just a wrapper of the android logger, only defaulting the tag to whatever i wished
 * @author Tim Stratton
 *
 */
public class Log
{
	private static final String tag = "AMVVM";
	
	private Log(){}
	
	public static void e(String message, Throwable tr)
	{
		android.util.Log.e(tag, message, tr);
	}
	
	public static void e(String message)
	{
		android.util.Log.e(tag, message);
	}
	
	public static void w(String message)
	{
		android.util.Log.w(tag, message);
	}
	
	public static void i(String message)
	{
		android.util.Log.i(tag, message);
	}
}
