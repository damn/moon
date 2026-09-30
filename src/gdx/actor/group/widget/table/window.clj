(ns gdx.actor.group.widget.table.window
  (:refer-clojure :exclude [class])
  (:require [gdx.change-listener :as change-listener]
            [gdx.actor.group.widget.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Label Skin TextButton Window)))

(def class Window)

(defn set-modal! [window modal?]
  (.setModal ^Window window modal?))

(defn add-close-button! [window skin]
  (table/add-cell! (.getTitleTable ^Window window)
             {:actor (doto (TextButton. "X" ^Skin skin)
                       (.addListener (change-listener/create
                                           (fn [_event _actor]
                                             (.remove ^Actor window)))))}))

(def ^:private set-opt-fns
  {:window/add-close-button? (fn [window skin _]
                               (add-close-button! window skin))})

(defn create [{:keys [title skin] :as opts}]
  (let [window (Window. ^String title ^Skin skin)]
    (table/set-opts! window opts)
    (doseq [[k v] opts :when (and (set-opt-fns k) v)]
      ((set-opt-fns k) window skin v))
    window))

(defn title-bar? [actor]
  (when (instance? Label actor)
    (when-let [p (.getParent ^Actor actor)]
      (when-let [p (.getParent ^Actor p)]
        (and (instance? Window p)
             (= (.getTitleLabel ^Window p) actor))))))
