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

package amvvm.interfaces;

/**
 * Defined a handler for IObservableObject object to signal
 * @author Tim Stratton
 *
 */
public interface IObjectListener
{
    public static class Utility
    {
        public static String generatePropagationId(String currentPropagationId, String currentSource)
        {
            if (currentSource == null || currentSource.equals(""))
                return currentPropagationId;
            else if (currentPropagationId == null || currentPropagationId.equals(""))
                return currentSource;
            else
                return currentSource + "." + currentPropagationId;
        }
    }

	/**
	 * Fired when listener is signalled of something, anything really.
	 */
    void onEvent(String propagationId);
}
