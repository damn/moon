(ns clojure.com.badlogic.gdx.input
  "Interface to the input facilities. This allows polling the state of the keyboard, the touch screen and the accelerometer. On
  some backends (desktop, gwt, etc) the touch screen is replaced by mouse input. The accelerometer is of course not available on
  all backends.
  
  Instead of polling for events, one can process all input events with an input processor. You can set the InputProcessor
  via the [set-input-processor!] method. It will be called before the render
  method in each frame.
  
  Keyboard keys are translated to the constants in [[clojure.com.badlogic.gdx.input-keys]] transparently on all systems. Do not use system specific key
  constants.
  
  The class also offers methods to use (and test for the presence of) other input systems like vibration, compass, on-screen
  keyboards, and cursor capture. Support for simple input dialogs is also provided."
  (:import (com.badlogic.gdx Input InputProcessor)))

(defn x
  "Returns the x coordinate of the last touch on touch screen devices and the current mouse position on desktop for the first
   pointer in screen coordinates. The screen origin is the top left corner."
  [^Input input]
  (.getX input))

(defn y
  "Returns the y coordinate of the last touch on touch screen devices and the current mouse position on desktop for the first
   pointer in screen coordinates. The screen origin is the top left corner."
  [^Input input]
  (.getY input))

(defn key-pressed?
  "Returns whether the key is pressed.
  
  - `key`: The key code as found in [[clojure.com.badlogic.gdx.input-keys]].
  
  Returns true or false."
  [^Input input key]
  (.isKeyPressed input key))

(defn key-just-pressed?
  "Returns whether the key has just been pressed.
  
  - `key`: The key code as found in [[clojure.com.badlogic.gdx.input-keys]].
  
  Returns true or false."
  [^Input input key]
  (.isKeyJustPressed input key))

(defn button-just-pressed?
  "Returns whether a given button has just been pressed. Button constants can be found in [[clojure.com.badlogic.gdx.input-buttons]]. On Android only the
  Buttons#LEFT constant is meaningful before version 4.0. On WebGL (GWT), only LEFT, RIGHT and MIDDLE buttons are supported.
  
  - `button`: the button to check.
  
  Returns true or false."
  [^Input input button]
  (.isButtonJustPressed input (int button)))

(defn set-input-processor!
  "Sets the input processor that will receive all touch and key input events. It will be called before the
  render method each frame.
  
  - `processor`: the InputProcessor"
  [^Input input ^InputProcessor processor]
  (.setInputProcessor input processor))
