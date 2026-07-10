(ns com.badlogic.gdx.scenes.scene2d.actor
  (:refer-clojure :exclude [new remove])
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor)))

(defn new [act! draw!]
  (proxy [Actor] []
    (act [delta]
      (act! this delta)
      (let [^Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha]
      (draw! this batch parent-alpha))))

(defn addListener [^Actor actor listener]
  (.addListener actor listener))

(defn getHeight [^Actor actor]
  (.getHeight actor))

(defn getName [^Actor actor]
  (.getName actor))

(defn getParent [^Actor actor]
  (.getParent actor))

(defn getStage [actor]
  (.getStage ^Actor actor))

(defn getUserObject [^Actor actor]
  (.getUserObject actor))

(defn getWidth [^Actor actor]
  (.getWidth actor))

(defn getX [^Actor actor]
  (.getX actor))

(defn getY [^Actor actor]
  (.getY actor))

(defn hit [^Actor actor x y touchable?]
  (.hit actor (float x) (float y) touchable?))

(defn remove [^Actor actor]
  (.remove actor))

(defn setName [^Actor actor name]
  (.setName actor name))

(defn setPosition
  ([^Actor actor x y]
   (.setPosition actor (float x) (float y)))
  ([^Actor actor x y align]
   (.setPosition actor (float x) (float y) align)))

(defn setTouchable [^Actor actor touchable]
  (.setTouchable actor touchable))

(defn setUserObject [^Actor actor user-object]
  (.setUserObject actor user-object))

(defn setVisible [^Actor actor visible?]
  (.setVisible actor visible?))

(defn stageToLocalCoordinates [^Actor actor ^Vector2 screen-coords]
  (.stageToLocalCoordinates actor screen-coords))

(defn isVisible [^Actor actor]
  (.isVisible actor))
