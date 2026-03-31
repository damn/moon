(ns clj.api.com.badlogic.gdx.scenes.scene2d.actor
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn create [{:keys [act! draw!]}]
  (proxy [Actor] []
    (act [delta]
      (when act!
        (act! this delta))
      (let [^Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha]
      (when draw!
        (draw! this batch parent-alpha)))))

(defn add-listener! [^Actor actor listener]
  (.addListener actor listener))

(defn user-object [^Actor actor]
  (.getUserObject actor))

(defn stage [^Actor actor]
  (.getStage actor))

(defn set-position! [^Actor actor [x y]]
  (.setPosition actor x y))

(defn set-visible! [^Actor actor visible?]
  (.setVisible actor visible?))

(defn visible? [^Actor actor]
  (.isVisible actor))

(defn parent [^Actor actor]
  (.getParent actor))

(defn remove! [^Actor actor]
  (.remove actor))

(defn set-name! [^Actor actor name]
  (.setName actor name))

(defn set-user-object! [^Actor actor object]
  (.setUserObject actor object))

(defn hit [^Actor actor [x y] touchable?]
  (.hit actor x y touchable?))

(defn x [^Actor actor]
  (.getX actor))

(defn y [^Actor actor]
  (.getY actor))

(defn stage->local-coordinates [^Actor actor vector2]
  (.stageToLocalCoordinates actor vector2))

(defn set-touchable! [^Actor actor touchable]
  (.setTouchable actor touchable))

(defn width [^Actor actor]
  (.getWidth actor))

(defn height [^Actor actor]
  (.getHeight actor))
