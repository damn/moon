(ns moon.scene2d.window
  (:require [moon.scene2d.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Label Skin TextButton Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener Layout)))

(defn add-close-button! [window skin]
  (table/add-cell! (.getTitleTable ^Window window)
             {:actor (doto (TextButton. "X" ^Skin skin)
                       (.addListener (proxy [ChangeListener] []
                                           (changed [_event _actor]
                                             (.remove ^Actor window)))))}))

(def ^:private set-opt-fns
  {:window/add-close-button? (fn [window skin _]
                               (add-close-button! window skin))})

(defn create [{:keys [title skin table/cell-defaults table/rows] :as opts}]
  (let [window (Window. ^String title ^Skin skin)]
    (when cell-defaults
      (table/set-cell-defaults! window cell-defaults))
    (when rows
      (table/add-rows! window rows)
      (.pack ^Layout window))
    (doseq [[k v] opts :when (and (set-opt-fns k) v)]
      ((set-opt-fns k) window skin v))
    window))

(defn title-bar? [actor]
  (when (instance? Label actor)
    (when-let [p (.getParent ^Actor actor)]
      (when-let [p (.getParent ^Actor p)]
        (and (instance? Window p)
             (= (.getTitleLabel ^Window p) actor))))))
