(ns clojure.gdx.scene2d.actor
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn set-touchable! [^Actor actor touchable]
  (.setTouchable actor touchable))

(defn set-name! [^Actor actor name]
  (.setName actor name))

(defn remove! [^Actor actor]
  (.remove actor))

(defn get-stage [^Actor actor]
  (.getStage actor))

(defn get-user-object [^Actor actor]
  (.getUserObject actor))

(defn get-parent [^Actor actor]
  (.getParent actor))

(defn get-name [^Actor actor]
  (.getName actor))

(defn get-width [^Actor actor]
  (.getWidth actor))

(defn get-height [^Actor actor]
  (.getHeight actor))

(defn visible? [^Actor actor]
  (.isVisible actor))

(defn get-x [^Actor actor]
  (.getX actor))

(defn get-y [^Actor actor]
  (.getY actor))

(defn stage->local-coordinates [^Actor actor vector2]
  (.stageToLocalCoordinates actor vector2))

(defn hit [^Actor actor [x y] touchable?]
  (.hit actor x y touchable?))

(defn set-position!
  ([^Actor actor [x y]]
   (.setPosition actor x y))
  ([^Actor actor [x y] align]
   (.setPosition actor x y align)))
