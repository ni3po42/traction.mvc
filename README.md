<h1>Traction MVC, v BETA 0.9.0 (soon)</h1>

<strong>0.9.0 Update is coming. Framework is now known as 'Traction MVC'. Stay tuned for updated docs.</strong>

<ul>
<li><a href="https://github.com/ni3po42/traction.mvc/wiki/How-does-it-work%3F">How's it work</a></li>
<li><a href="https://github.com/ni3po42/traction.mvc/wiki/Getting-Started">Getting started</a></li>
<li><a href="https://github.com/ni3po42/traction.mvc/wiki/How-to-Contribute">How to contribute</a></li>
<li><a href="https://github.com/ni3po42/traction.mvc/wiki/Upcoming-features">Future Features</a></li>
</ul>

<h2>
INTRODUCTION
</h2>

Traction MVC is a light-weight framework for implementing a model,view,controller pattern within your Android project. It handles the details of connecting data and events from the UI side to your models, allowing you to keep a more strict separation of concerns within your project.

<h2>
GOALS
</h2>

It's there when you need it, but doesn't get in your way.

In designing this library, I intended it to solve a couple of issues with general Android development while trying to avoid many constraints other libraries put on the developer to use it. Activities and Fragments work as you would expect, and for the most part, you don't need to alter your approach much. The library introduces a few classes that are required for notifying updates to and from the UI, but doesn't lock you into using specific base classes (most core objects have interfaces and are designed to pass the functionality of the implementers to your own objects: for instance, if you don't want to use the base Activity or Fragment in the library, you can use your own and extend it with a helper class that takes care of the heavy lifting for you).

<h2>
No referencing android.view or android.widget (almost)
</h2>

There is (almost) no need to reference any view objects in the activity or fragment. In the near future, a wiki will be set up to better explain current features and future features. But that aside, nothing would make me happier then to never have to use the 'findViewById' method ever again (including the 'findFragmentById' method as well).

<h2>
No need to replace existing views in layouts!
</h2>

This framework 'extends' the capabilities of existing view by creating objects that work beside the views; this means you can continue using all the views you have; android core views/widgets and your own. The framework will identify the base view/widgets used and apply particular elements to it to communicate back to the controller. If the framework doesn't natively support your custom view, you can easily extend your view to handle the binding, or create a seperate binding object to use (especially useful if you can't extend the custom views).

Basic design documents have been created and I expect to have more detailed documentation in the coming weeks. Until then, a Demo application was included in the interim until the wiki is fully up; this will assist developers for now on how to use the code.
