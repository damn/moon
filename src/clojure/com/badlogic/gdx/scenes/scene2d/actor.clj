(ns clojure.com.badlogic.gdx.scenes.scene2d.actor
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

(defn x [^Actor actor]
  (.getX actor))

(defn y [^Actor actor]
  (.getY actor))

(defn stage [^Actor actor]
  (.getStage actor))

(defn add-listener! [^Actor actor ^EventListener listener]
  (.addListener actor listener))

(defn remove! [^Actor actor]
  (.remove actor))

(defn set-touchable! [^Actor actor ^Touchable touchable]
  (.setTouchable actor touchable))

(defn user-object [^Actor actor]
  (.getUserObject actor))

(defn set-user-object! [^Actor actor obj]
  (.setUserObject actor obj))

(defn name [^Actor actor]
  (.getName actor))

(defn set-name! [^Actor actor name]
  (.setName actor name))

(defn parent [^Actor actor]
  (.getParent actor))

(defn stage->local-coordinates [^Actor actor ^Vector2 v2]
  (.stageToLocalCoordinates actor v2))

(defn hit [^Actor actor x y touchable?]
  (.hit actor (float x) (float y) touchable?))

(defn visible? [^Actor actor]
  (.isVisible actor))

(defn set-visible! [^Actor actor v]
  (.setVisible actor v))

(defn set-position!
  ([^Actor actor x y]
   (.setPosition actor (float x) (float y)))
  ([^Actor actor x y align]
   (.setPosition actor (float x) (float y) align)))

(defn first-ascendant [^Actor actor class]
  (.firstAscendant actor class))
