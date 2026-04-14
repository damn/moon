(ns moon.ui.actor
  (:refer-clojure :exclude [name])
  (:require [clj.api.com.badlogic.gdx.math.vector2 :as vector2]
            [clj.api.com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clj.api.com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.click-listener :as click-listener]
            [clj.api.com.badlogic.gdx.utils.align :as align]))

; TODO see what is only used @ opts, no need for API then

(def name actor/name)
(def x actor/x)
(def y actor/y)
(def width actor/width)
(def height actor/height)

(defn stage->local-coordinates [actor xy]
  (vector2/->clj (actor/stage->local-coordinates actor (vector2/->java xy))))

(defn add-listener! [actor [listener-k listener-params]]
  (actor/add-listener! actor
                       (case listener-k
                         :listener/change (let [f listener-params]
                                            (change-listener/create f))
                         :listener/text-tooltip (let [[tooltip skin] listener-params]
                                                  (text-tooltip/create tooltip skin))
                         :listener/click (let [f listener-params]
                                           (click-listener/create f))
                         )))

(def user-object actor/user-object)

(def stage actor/stage)

; used?
(def set-name! actor/set-name!)

(def set-user-object! actor/set-user-object!)

(def set-position! actor/set-position!)

(def set-visible! actor/set-visible!)

; only used @ opts
(def set-touchable! actor/set-touchable!)

(def visible? actor/visible?)

; used with stage->local-coordinates
(def hit actor/hit)
(def remove! actor/remove!)
(def parent actor/parent)

; not part of minimal clojure.gdx API ?
(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))

(defn- ui-type->class [k]
  (case k
    :ui/window com.badlogic.gdx.scenes.scene2d.ui.Window))

(defn find-ancestor
  [actor ui-type-k]
  (if-let [parent (parent actor)]
    (if (instance? (ui-type->class ui-type-k) parent)
      parent
      (find-ancestor parent ui-type-k))
    (throw (Error. (str "Actor has no parent window " actor)))))

(defn set-opts!
  [actor opts]
  (when-let [user-object (:actor/user-object opts)]
    (actor/set-user-object! actor user-object))

  (when (:actor/position opts)
    (let [[x y align] (:actor/position opts)]
      (if align
        (actor/set-position! actor x y (align/k->value align))
        (actor/set-position! actor [x y]))))

  (when (contains? opts :actor/visible?)
    (actor/set-visible! actor (:actor/visible? opts)))

  (when-let [touchable (:actor/touchable opts)]
    (actor/set-touchable! actor (case touchable
                                  :touchable/disabled touchable/disabled)))

  (when-let [name (:actor/name opts)]
    (actor/set-name! actor name))

  (when-let [listeners (:actor/listeners opts)]
    (doseq [listener listeners]
      (add-listener! actor listener))))

(defn create
  [opts]
  (doto (actor/create opts)
    (set-opts! opts)))
