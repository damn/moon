(ns gdx.scenes.scene2d.ui.window
  (:require [clojure.ui.table.set-opts :refer [set-opts!]]
            [clojure.ui.window :as window]))

(defn create
  [{:keys [title skin] :as opts}]
  (let [window (window/create title skin)]
    (set-opts! window opts)
    window))
