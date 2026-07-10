(ns clojure.editor.create-widget-s-enum
  (:require [clojure.editor.create-widget :refer [create-widget]]
            [clojure.edn.v-to-str :refer [->edn-str]]
            [com.badlogic.gdx.scenes.scene2d.ui.select-box :as select-box]))

(defmethod create-widget :s/enum
  [schema v {:keys [ctx/skin]}]
  (doto (select-box/new skin)
    (select-box/setItems (map ->edn-str (rest schema)))
    (select-box/setSelected (->edn-str v))))
