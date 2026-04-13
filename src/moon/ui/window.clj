(ns moon.ui.window
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [moon.ui.table :as table]
            [moon.window :as x-window]))

(defn set-opts! [window opts]
  (when-let [skin (:window/close-button? opts)]
    (x-window/add-close-button! window skin)))

(defn create
  [{:keys [title skin] :as opts}]
  (doto (window/create title skin)
    (set-opts! opts)
    (table/set-opts! opts)))
