amvvm, v ALPHA 0.1.2

INTRODUCTION

amvvm is a light-weight framework for implementing a model,view,view-model pattern within your Android project. It handles the details of connecting data and events from the UI side to your view-models and models, allowing you to keep a more strict separation of concerns within your project.

GOALS

It's there when you need it, but doesn't get in your way.

In designing this library, I intended it to solve a couple of issues with general Android development while trying to avoid many constraints other libraries put on the developer to use it. Activities and Fragments work as you would expect, and for the most part, you don't need to alter your approach much. The library introduces a few classes that are required for notifying updates to and from the UI, but doesn't lock you into using specific base classes (most core objects have interfaces and are designed to pass the functionality of the implementers to your own objects: for instance, if you don't want to use the base Activity or Fragment in the library, you can use your own and extend it with a helper class that takes care of the heavy lifting for you).

No referencing android.view or android.widget (almost)

There is (almost) no need to reference any view objects in the activity or fragment (here on referred to as the 'view-model'). This library is still alpha, and many more features are yet to be fully integrated. In the near future, a wiki will be set up to better explain current features and future features. But that aside, nothing would make me happier then to never have to use the 'findViewById' method every again (also the 'findFragmentById' also).

No need to replace existing views in layouts!

This framework 'extends' the capabilities of existing view by creating objects that work beside the views; this means you can continue using all the views you have; android core views/widgets and your own. The framework will identify the bases view/widgets used and apply particular elements to it to communicate back to the view-model.

I don't have detailed documentation compiled yet, but a Demo application was included in the interim until the wiki is up; this will assist developers for now on how to use the code.
