(ns clojure.gdx.scene2d.actor
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn create
  [{:keys [act! draw!]}]
  (proxy [Actor] []
    (act [delta]
      (when act!
        (act! this delta))
      (let [^Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha]
      (when draw!
        (draw! this batch parent-alpha)))))

(defn stage [^Actor actor]
  (.getStage actor))

(defn set-name! [^Actor actor name]
  (.setName actor name))

(defn set-user-object! [^Actor actor object]
  (.setUserObject actor object))

(defn visible? [^Actor actor]
  (.isVisible actor))

(defn hit [^Actor actor [x y] touchable?]
  (.hit actor x y touchable?))

(defn parent [^Actor actor]
  (.getParent actor))

(defn set-visible! [^Actor actor visible?]
  (.setVisible actor visible?))

(defn set-position!
  ([^Actor actor [x y]]
   (.setPosition actor x y))
  ([^Actor actor [x y] align]
   (.setPosition actor x y align)))

(defn set-touchable! [^Actor actor touchable]
  (.setTouchable actor touchable))

(defn stage->local-coordinates [^Actor actor vector2]
  (.stageToLocalCoordinates actor vector2))
