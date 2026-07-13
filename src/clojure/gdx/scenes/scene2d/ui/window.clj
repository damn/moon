(ns clojure.gdx.scenes.scene2d.ui.window
  (:require [clojure.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [clojure.gdx.scenes.scene2d.ui.table :as table]))

(defn add-close-button! [window skin]
  (table/add-cell! (window/getTitleTable window)
             {:actor (doto (text-button/new "X" skin)
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
