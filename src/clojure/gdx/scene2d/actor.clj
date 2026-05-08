(ns clojure.gdx.scene2d.actor
  (:refer-clojure :exclude [name])
  (:require [com.badlogic.gdx.math.vector2 :as vector2]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.scenes.scene2d.utils.click-listener :as click-listener]
            [com.badlogic.gdx.utils.align :as align]
            moon.ui.actor))

(defn- ui-type->class [k]
  (case k
    :ui/window com.badlogic.gdx.scenes.scene2d.ui.Window))

(defn- button-class? [actor]
  (some #(= com.badlogic.gdx.scenes.scene2d.ui.Button %) (supers (class actor))))

(extend com.badlogic.gdx.scenes.scene2d.Actor
  moon.ui.actor/Actor
  {:name actor/name
   :x actor/x
   :y actor/y
   :width actor/width
   :height actor/height
   :user-object actor/user-object
   :stage actor/stage
   :set-name! actor/set-name!
   :set-user-object! actor/set-user-object!
   :set-position! actor/set-position!
   :set-visible! actor/set-visible!
   :set-touchable! actor/set-touchable!
   :visible? actor/visible?
   :hit actor/hit
   :remove! actor/remove!
   :parent actor/parent
   :stage->local-coordinates
   (fn [actor xy]
     (vector2/->clj (actor/stage->local-coordinates actor (vector2/->java xy))))
   :add-listener!
   (fn [actor [listener-k listener-params]]
     (actor/add-listener! actor
                          (case listener-k
                            :listener/change (let [f listener-params]
                                               (change-listener/create f))
                            :listener/text-tooltip (let [[tooltip skin] listener-params]
                                                     (text-tooltip/create tooltip skin))
                            :listener/click (let [f listener-params]
                                              (click-listener/create f)))))

   :find-ancestor
   (fn
     [actor ui-type-k]
     (if-let [parent (actor/parent actor)]
       (if (instance? (ui-type->class ui-type-k) parent)
         parent
         (moon.ui.actor/find-ancestor parent ui-type-k))
       (throw (Error. (str "Actor has no parent window " actor)))))

   :button?
   (fn [actor]
     (or (button-class? actor)
         (and (actor/parent actor)
              (button-class? (actor/parent actor)))))

   :window-title-bar?
   ; FIXME does not work
   (fn [actor]
     (when (instance? com.badlogic.gdx.scenes.scene2d.ui.Label actor)
       (when-let [p (actor/parent actor)]
         (when-let [p (actor/parent p)]
           (and (instance? com.badlogic.gdx.scenes.scene2d.ui.Window actor)
                (= (window/title-label p) actor))))))

   :toggle-visible!
   (fn [actor]
     (actor/set-visible! actor (not (actor/visible? actor))))

   :set-opts!
   (fn [actor opts]
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
         (moon.ui.actor/add-listener! actor listener))))})

(defmethod moon.ui.actor/create :ui/actor
  [opts]
  (doto (actor/create opts)
    (moon.ui.actor/set-opts! opts)))
