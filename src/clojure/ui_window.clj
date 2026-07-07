(ns clojure.ui-window
  (:require [clojure.window :as window]
            [clojure.table-set-opts :refer [set-opts!]]))

(defn f [{:keys [title skin]}]
  (window/new title skin))

(defn create
  [opts]
  (let [window (f opts)]
    (set-opts! window opts)
    window))
