(ns clojure.ui-label
  (:require [gdl.label :as label]))

(defn create
  [{:keys [text skin]}]
  (label/new text skin))
