(ns gdx.actor.group.widget.table.window
  (:refer-clojure :exclude [class])
  (:require [gdx.actor :as actor]
            [gdx.actor.widget.label :as label]
            [gdx.actor.group.widget.table.button.text :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [gdx.change-listener :as change-listener]
            [gdx.actor.group.widget.table :as table]))

(def class window/class)

(defn set-modal! [window modal?]
  (window/setModal window modal?))

(defn add-close-button! [window skin]
  (table/add-cell! (window/getTitleTable window)
             {:actor (doto (text-button/create "X" skin)
                       (actor/add-listener! (change-listener/create
                                           (fn [_event _actor]
                                             (actor/remove! window)))))}))

(def ^:private set-opt-fns
  {:window/add-close-button? (fn [window skin _]
                               (add-close-button! window skin))})

(defn create [{:keys [title skin] :as opts}]
  (let [window (window/new title skin)]
    (table/set-opts! window opts)
    (doseq [[k v] opts :when (and (set-opt-fns k) v)]
      ((set-opt-fns k) window skin v))
    window))

(defn title-bar? [actor]
  (when (instance? label/class actor)
    (when-let [p (actor/get-parent actor)]
      (when-let [p (actor/get-parent p)]
        (and (instance? window/class p)
             (= (window/getTitleLabel p) actor))))))
