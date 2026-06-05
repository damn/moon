(ns editor.widget.enum
  (:require [clojure.edn-str :refer [->edn-str]]
            [clojure.edn :as edn]
            [clojure.gdx.scene2d.ui.select-box :as select-box]
            [editor.widget :as widget]))

(defmethod widget/create :s/enum [schema v {:keys [ctx/skin]}]
  (select-box/create
   {:skin skin
    :items (map ->edn-str (rest schema))
    :selected (->edn-str v)}))

(defmethod widget/value :s/enum [_  widget _schemas]
  (edn/read-string (select-box/selected widget)))
