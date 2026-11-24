(ns clojure.com.badlogic.gdx.scenes.scene2d.actor
  "2D scene graph node. An actor has a position, rectangular size, origin, scale, rotation, Z index, and color. The position
  corresponds to the unrotated, unscaled bottom left corner of the actor. The position is relative to the actor's parent. The
  origin is relative to the position and is used for scale and rotation.
  
  An actor has a list of in progress action that are applied to the actor (often over time). These are generally
  used to change the presentation of the actor (moving it, resizing it, etc). See [clojure.com.badlogic.gdx.scenes.scene2d.stage/act!], action, and its
  many subclasses.
  
  An actor has two kinds of listeners associated with it: \"capture\" and regular. The listeners are notified of events the actor
  or its children receive. The regular listeners are designed to allow an actor to respond to events that have been delivered.
  The capture listeners are designed to allow a parent or container actor to handle events before child actors. See fire
  for more details.
  
  An input listener can receive all the basic input events. More complex listeners (like [[clojure.com.badlogic.gdx.scenes.scene2d.utils.click-listener]] and
  actor gesture listener) can listen for and combine primitive events and recognize complex interactions like multi-touch
  or pinch."
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor EventListener Touchable))
  (:refer-clojure :exclude [name]))

(defn create [{:keys [act! draw!]}]
  (proxy [Actor] []
    (act [delta]
      (act! this delta)
      (let [^Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha]
      (draw! this batch parent-alpha))))

(defn width [^Actor actor]
  (.getWidth actor))

(defn height [^Actor actor]
  (.getHeight actor))

(defn x
  "Returns the X position of the actor's left edge."
  [^Actor actor]
  (.getX actor))

(defn y
  "Returns the Y position of the actor's bottom edge."
  [^Actor actor]
  (.getY actor))

(defn stage [^Actor actor]
  (.getStage actor))

(defn add-listener!
  "Add a listener to receive events that [hit] this actor. See fire."
  [^Actor actor ^EventListener listener]
  (.addListener actor listener))

(defn remove!
  "Removes this actor from its parent, if it has a parent."
  [^Actor actor]
  (.remove actor))

(defn set-touchable!
  "Determines how touch events are distributed to this actor. Default is enabled."
  [^Actor actor ^Touchable touchable]
  (.setTouchable actor touchable))

(defn user-object [^Actor actor]
  (.getUserObject actor))

(defn set-user-object!
  "Sets an application specific object for convenience."
  [^Actor actor obj]
  (.setUserObject actor obj))

(defn name [^Actor actor]
  (.getName actor))

(defn set-name!
  "Set the actor's name, which is used for identification convenience and by to string.
  
  - `name`: May be null."
  [^Actor actor name]
  (.setName actor name))

(defn parent [^Actor actor]
  (.getParent actor))

(defn stage->local-coordinates
  "Transforms the specified point in the stage's coordinates to the actor's local coordinate system."
  [^Actor actor ^Vector2 v2]
  (.stageToLocalCoordinates actor v2))

(defn hit [^Actor actor x y touchable?]
  (.hit actor (float x) (float y) touchable?))

(defn visible? [^Actor actor]
  (.isVisible actor))

(defn set-visible!
  "If false, the actor will not be drawn and will not receive touch events. Default is true."
  [^Actor actor v]
  (.setVisible actor v))

(defn set-position!
  "Sets the position of the actor's bottom left corner."
  ([^Actor actor x y]
   (.setPosition actor (float x) (float y)))
  ([^Actor actor x y align]
   (.setPosition actor (float x) (float y) align)))

(defn first-ascendant [^Actor actor class]
  (.firstAscendant actor class))
