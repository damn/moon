(ns clojure.ui-text-button
  (:require [clojure.text-button :as text-button]))

(defn create
  [{:keys [text skin]}]
  (text-button/new text skin))
