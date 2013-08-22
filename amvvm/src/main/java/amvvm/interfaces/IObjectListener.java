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


import java.util.EventObject;

/**
 * Defined a handler for IObservableObject object to signal
 * @author Tim Stratton
 *
 */
public interface IObjectListener
{	
	public static class EventArg
	{
		private String propertyName;
        private String propagationId;
        private Object source;

        public void setAll(String propertyName, Object source)
        {
            this.propertyName = propertyName;
            this.source = source;
        }

        public Object getSource()
        {
            return source;
        }

        public String getPropertyName()
        {
            return propertyName;
        }

		public void recycle()
		{
			setAll(null, null);
            this.propagationId = null;
		}

        public void setPropagationId(String id)
        {
            this.propagationId = id;
        }

        public String getPropagationId()
        {
            return this.propagationId;
        }

        public String generateNextPropagationId()
        {
            if (propertyName == null || propertyName.equals(""))
                return propagationId;
            else if (propagationId != null && !propagationId.equals(""))
                return propertyName + "." + propagationId;
            else
                return propertyName;
        }
	}	
	
	/**
	 * Fired when listener is signalled of something, anything really.
	 * @param arg
	 */
	void onEvent(EventArg arg);
	
}
