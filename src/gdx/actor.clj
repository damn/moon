(ns gdx.actor
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

(defn add-listener! [a listener]
  (.addListener ^Actor a listener))

(defn get-height [a]
  (.getHeight ^Actor a))

(defn get-name [a]
  (.getName ^Actor a))

(defn get-parent [a]
  (.getParent ^Actor a))

(defn get-stage [a]
  (.getStage ^Actor a))

(defn get-user-object [a]
  (.getUserObject ^Actor a))

(defn get-width [a]
  (.getWidth ^Actor a))

(defn get-x [a]
  (.getX ^Actor a))

(defn get-y [a]
  (.getY ^Actor a))

(defn hit [a x y touchable?]
  (.hit ^Actor a (float x) (float y) touchable?))

(defn remove! [a]
  (.remove ^Actor a))

(defn set-name! [a name]
  (.setName ^Actor a name))

(defn set-position!
  ([a x y]
   (.setPosition ^Actor a (float x) (float y)))
  ([a x y align]
   (.setPosition ^Actor a (float x) (float y) align)))

(defn set-touchable! [a touchable]
  (.setTouchable ^Actor a touchable))

(defn set-user-object! [a user-object]
  (.setUserObject ^Actor a user-object))

(defn set-visible! [a visible?]
  (.setVisible ^Actor a visible?))

(defn stage-to-local-coordinates [a screen-coords]
  (.stageToLocalCoordinates ^Actor a ^Vector2 screen-coords))

(defn visible? [a]
  (.isVisible ^Actor a))

(defn find-ancestor [a pred?]
  (loop [actor a]
    (if-let [p (get-parent actor)]
      (if (pred? p)
        p
        (recur p))
      (throw (Error. (str "Actor has no matching ancestor " actor))))))
