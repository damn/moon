(ns clojure.ui-text-button
  (:require [gdl.scenes.scene2d.ui.text-button :as text-button]))

(defn create
  [{:keys [text skin]}]
  (text-button/new text skin))
