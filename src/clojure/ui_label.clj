(ns clojure.ui-label
  (:require [gdl.scenes.scene2d.ui.label :as label]))

(defn create
  [{:keys [text skin]}]
  (label/new text skin))
