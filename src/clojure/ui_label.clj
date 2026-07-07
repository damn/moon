(ns clojure.ui-label
  (:require [clojure.label :as label]))

(defn create
  [{:keys [text skin]}]
  (label/new text skin))
