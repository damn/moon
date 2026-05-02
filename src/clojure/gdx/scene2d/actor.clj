(ns clojure.gdx.scene2d.actor
  (:refer-clojure :exclude [name])
  (:require [com.badlogic.gdx.math.vector2 :as vector2]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [clojure.gdx.scene2d.ui.text-tooltip :as text-tooltip]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [clojure.gdx.scene2d.utils.click-listener :as click-listener]
            [clojure.gdx.utils.align :as align])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button
                                               Label
                                               Window)))

(defn- ui-type->class [k]
  (case k
    :ui/window com.badlogic.gdx.scenes.scene2d.ui.Window))

(defn- button-class? [actor]
  (some #(= Button %) (supers (class actor))))

(def name actor/name)
(def x actor/x)
(def y actor/y)
(def width actor/width)
(def height actor/height)
(def user-object actor/user-object)
(def stage actor/stage)
(def set-name! actor/set-name!)
(def set-user-object! actor/set-user-object!)
(def set-position! actor/set-position!)
(def set-visible! actor/set-visible!)
(def set-touchable! actor/set-touchable!)
(def visible? actor/visible?)
(def hit actor/hit)
(def remove! actor/remove!)
(def parent actor/parent)

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
                                           (click-listener/create f)))))


(defn find-ancestor
  [actor ui-type-k]
  (if-let [parent (parent actor)]
    (if (instance? (ui-type->class ui-type-k) parent)
      parent
      (find-ancestor parent ui-type-k))
    (throw (Error. (str "Actor has no parent window " actor)))))

(defn button? [actor]
  (or (button-class? actor)
      (and (parent actor)
           (button-class? (parent actor)))))

; FIXME does not work
(defn window-title-bar? [actor]
  (when (instance? Label actor)
    (when-let [p (parent actor)]
      (when-let [p (parent p)]
        (and (instance? Window actor)
             (= (Window/.getTitleLabel p) actor))))))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))

(defn set-opts! [actor opts]
  (when-let [user-object (:actor/user-object opts)]
    (set-user-object! actor user-object))

  (when (:actor/position opts)
    (let [[x y align] (:actor/position opts)]
      (if align
        (set-position! actor x y (align/k->value align))
        (set-position! actor [x y]))))

  (when (contains? opts :actor/visible?)
    (set-visible! actor (:actor/visible? opts)))

  (when-let [touchable (:actor/touchable opts)]
    (set-touchable! actor (case touchable
                            :touchable/disabled touchable/disabled)))

  (when-let [name (:actor/name opts)]
    (set-name! actor name))

  (when-let [listeners (:actor/listeners opts)]
    (doseq [listener listeners]
      (add-listener! actor listener))))

(defmulti create :type)

(defmethod create :ui/actor
  [opts]
  (doto (actor/create opts)
    (set-opts! opts)))
