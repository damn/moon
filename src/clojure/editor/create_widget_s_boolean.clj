(ns clojure.editor.create-widget-s-boolean
  (:require [com.badlogic.gdx.scenes.scene2d.ui.check-box :as check-box]
            [clojure.editor.create-widget :refer [create-widget]]))

(defmethod create-widget :s/boolean
  [_ checked? {:keys [ctx/skin]}]
  (doto (check-box/new "" skin)
    (check-box/setChecked checked?)))
