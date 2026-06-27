(ns com.badlogic.gdx.scenes.scene2d.actor
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn proxy-actor
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

(defn visible? [^Actor actor]
  (.isVisible actor))

(defn stage->local-coordinates [^Actor actor vector2]
  (.stageToLocalCoordinates actor vector2))

(defn set-visible! [^Actor actor visible?]
  (.setVisible actor visible?))

(defn set-user-object! [^Actor actor user-object]
  (.setUserObject actor user-object))

(defn set-touchable! [^Actor actor touchable]
  (.setTouchable actor touchable))

(defn set-position! [^Actor actor x y]
  (.setPosition actor x y))

(defn set-position-with-align! [^Actor actor x y align]
  (.setPosition actor x y align))

(defn set-name! [^Actor actor name]
  (.setName actor name))

(defn remove! [^Actor actor]
  (.remove actor))

(defn hit [^Actor actor x y touchable?]
  (.hit actor x y touchable?))

(defn get-y [^Actor actor]
  (.getY actor))

(defn get-x [^Actor actor]
  (.getX actor))

(defn get-width [^Actor actor]
  (.getWidth actor))

(defn get-user-object [^Actor actor]
  (.getUserObject actor))

(defn get-stage [^Actor actor]
  (.getStage actor))

(defn get-parent [^Actor actor]
  (.getParent actor))

(defn get-name [^Actor actor]
  (.getName actor))

(defn get-height [^Actor actor]
  (.getHeight actor))

(defn add-listener! [^Actor actor listener]
  (.addListener actor listener))

(defn type-hint
  ^Actor
  [obj]
  obj)
