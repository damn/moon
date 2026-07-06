(ns gdx.scene2d.ui.window
  (:require [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [gdx.scene2d.ui.table.set-opts :refer [set-opts!]]))

(defn f [{:keys [title skin]}]
  (window/new title skin))

(defn create
  [opts]
  (let [window (f opts)]
    (set-opts! window opts)
    window))
