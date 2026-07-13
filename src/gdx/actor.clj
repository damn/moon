(ns gdx.actor
  (:refer-clojure :exclude [new remove])
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn new [act! draw!]
  (actor/new act! draw!))

(defn add-listener! [a listener]
  (actor/addListener a listener))

(defn get-height [a]
  (actor/getHeight a))

(defn get-name [a]
  (actor/getName a))

(defn get-parent [a]
  (actor/getParent a))

(defn get-stage [a]
  (actor/getStage a))

(defn get-user-object [a]
  (actor/getUserObject a))

(defn get-width [a]
  (actor/getWidth a))

(defn get-x [a]
  (actor/getX a))

(defn get-y [a]
  (actor/getY a))

(defn hit [a x y touchable?]
  (actor/hit a x y touchable?))

(defn remove! [a]
  (actor/remove a))

(defn set-name! [a name]
  (actor/setName a name))

(defn set-position!
  ([a x y]
   (actor/setPosition a x y))
  ([a x y align]
   (actor/setPosition a x y align)))

(defn set-touchable! [a touchable]
  (actor/setTouchable a touchable))

(defn set-user-object! [a user-object]
  (actor/setUserObject a user-object))

(defn set-visible! [a visible?]
  (actor/setVisible a visible?))

(defn stage-to-local-coordinates [a screen-coords]
  (actor/stageToLocalCoordinates a screen-coords))

(defn visible? [a]
  (actor/isVisible a))

(defn find-ancestor [a pred?]
  (loop [actor a]
    (if-let [p (get-parent actor)]
      (if (pred? p)
        p
        (recur p))
      (throw (Error. (str "Actor has no matching ancestor " actor))))))
