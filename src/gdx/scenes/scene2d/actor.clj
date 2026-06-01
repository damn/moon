(ns gdx.scenes.scene2d.actor
  (:refer-clojure :exclude [name])
  (:require [clojure.gdx.math.vector2 :as vector2]
            [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.scene2d.listener :as listener]
            [clojure.gdx.scene2d.touchable :as touchable]
            [clojure.gdx.utils.align :as align]))

(def name actor/name)
(def x actor/x)
(def y actor/y)
(def width actor/width)
(def height actor/height)
(def user-object actor/user-object)
(def stage actor/stage)
(def set-name! actor/set-name!)
(def set-user-object! actor/set-user-object!)
(def visible? actor/visible?)
(def hit actor/hit)
(def remove! actor/remove!)
(def parent actor/parent)
(def set-visible! actor/set-visible!)

(defn set-position!
  ([actor xy]
   (actor/set-position! actor xy))
  ([actor xy align]
   (actor/set-position! actor xy (align/k->value align))))

(defn set-touchable! [actor touchable]
  (actor/set-touchable! actor (touchable/k->value touchable)))

(defn add-listener! [actor [listener-k listener-params]]
  (actor/add-listener! actor (listener/create [listener-k listener-params]))) ; 30 places ...

(defn stage->local-coordinates [actor xy]
  (vector2/->clj (actor/stage->local-coordinates actor (vector2/create xy))))

(defn find-ancestor [actor pred]
  (if-let [p (parent actor)]
    (if (pred p)
      p
      (find-ancestor p pred))
    (throw (Error. (str "Actor has no parent window " actor)))))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))

(defn set-opts! [actor opts]
  (when-let [user-object (:actor/user-object opts)]
    (set-user-object! actor user-object))
  (when (:actor/position opts)
    (let [[x y align] (:actor/position opts)]
      (if align
        (set-position! actor [x y] align)
        (set-position! actor [x y]))))
  (when (contains? opts :actor/visible?)
    (set-visible! actor (:actor/visible? opts)))
  (when-let [touchable (:actor/touchable opts)]
    (set-touchable! actor touchable))
  (when-let [name (:actor/name opts)]
    (set-name! actor name))
  (when-let [listeners (:actor/listeners opts)]
    (doseq [listener listeners]
      (add-listener! actor listener))))

(defn create [opts]
  (doto (actor/create opts)
    (set-opts! opts)))
