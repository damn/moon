(ns clojure.ui-select-box
  (:require [gdl.select-box :as select-box]))

(defn create
  [{:keys [items selected skin]}]
  (doto (select-box/new skin)
    (select-box/set-items! items)
    (select-box/set-selected! selected)))
