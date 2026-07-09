(ns com.badlogic.gdx.scenes.scene2d.actor
  (:refer-clojure :exclude [new remove])
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor)))

(defn add-listener [actor listener]
  (Actor/.addListener actor listener))

(defn get-height [^Actor actor]
  (Actor/.getHeight actor))

(defn get-name [^Actor actor]
  (Actor/.getName actor))

(defn get-parent [^Actor actor]
  (Actor/.getParent actor))

(defn get-stage [actor]
  (Actor/.getStage actor))

(defn get-user-object [^Actor actor]
  (Actor/.getUserObject actor))

(defn get-width [^Actor actor]
  (Actor/.getWidth actor))

(defn get-x [^Actor actor]
  (Actor/.getX actor))

(defn get-y [^Actor actor]
  (Actor/.getY actor))

(defn hit [^Actor actor x y touchable?]
  (Actor/.hit actor (float x) (float y) touchable?))

(defn new [act! draw!]
  (proxy [Actor] []
    (act [delta]
      (act! this delta)
      (let [^Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha]
      (draw! this batch parent-alpha))))

(defn remove [^Actor actor]
  (Actor/.remove actor))

(defn set-name [^Actor actor name]
  (Actor/.setName actor name))

(defn set-position
  ([^Actor actor x y]
   (Actor/.setPosition actor (float x) (float y)))
  ([^Actor actor x y align]
   (Actor/.setPosition actor (float x) (float y) align)))

(defn set-touchable [^Actor actor touchable]
  (Actor/.setTouchable actor touchable))

(defn set-user-object [^Actor actor user-object]
  (Actor/.setUserObject actor user-object))

(defn set-visible [^Actor actor visible?]
  (Actor/.setVisible actor visible?))

(defn stage-to-local-coordinates [^Actor actor ^Vector2 screen-coords]
  (Actor/.stageToLocalCoordinates actor screen-coords))

(defn visible? [^Actor actor]
  (Actor/.isVisible actor))
