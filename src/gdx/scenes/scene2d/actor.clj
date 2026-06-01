(ns gdx.scenes.scene2d.actor
  (:require [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.scene2d.actor.create :refer [create-actor]]
            [clojure.gdx.scene2d.actor.add-listener :refer [add-listener!]]
            [clojure.gdx.scene2d.actor.parent :refer [actor-parent]]
            [clojure.gdx.scene2d.actor.set-visible :refer [set-visible!]]
            [clojure.gdx.scene2d.actor.visible :refer [visible?]]
            [clojure.gdx.scene2d.actor.set-user-object :refer [set-user-object!]]
            [clojure.gdx.scene2d.actor.set-touchable :refer [set-touchable!]]
            [clojure.gdx.scene2d.actor.set-position :refer [set-position!]]))

(defn find-ancestor [actor pred]
  (if-let [p (actor-parent actor)]
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
    (actor/set-name! actor name))
  (when-let [listeners (:actor/listeners opts)]
    (doseq [listener listeners]
      (add-listener! actor listener))))

(defn create [opts]
  (doto (create-actor opts)
    (set-opts! opts)))
