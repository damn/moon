(ns clojure.gdx.scenes.scene2d.actor
  (:refer-clojure :exclude [name])
  (:require [com.badlogic.gdx.math.vector2 :as vector2]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.scenes.scene2d.utils.click-listener :as click-listener]
            [com.badlogic.gdx.utils.align :as align]))

; TODO some not need as in 'set-opts!' ??? (set-name!, set-position!/)?

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

(defn set-position!
  ([actor p]
   (actor/set-position! actor p))
  ([actor x y align]
   (actor/set-position! actor x y (align/k->value align))))

(def set-visible! actor/set-visible!)

(defn set-touchable! [actor touchable]
  (actor/set-touchable! actor (touchable/k->value touchable)))

(defn add-listener! [actor [listener-k listener-params]]
  (actor/add-listener! actor
                       (case listener-k
                         :listener/change (change-listener/create listener-params)
                         :listener/text-tooltip (text-tooltip/create listener-params)
                         :listener/click (click-listener/create listener-params))))

(defn stage->local-coordinates [actor xy]
  (vector2/->clj (actor/stage->local-coordinates actor (vector2/->java xy))))

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
        (set-position! actor x y align)
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

(defn create
  [opts]
  (doto (actor/create opts)
    (set-opts! opts)))
