(ns clojure.editor.create-widget-s-enum
  (:require [clojure.editor.create-widget :refer [create-widget]]
            [clojure.edn-str :refer [->edn-str]]
            [clojure.ui-select-box :as select-box]))

(defmethod create-widget :s/enum
  [schema v {:keys [ctx/skin]}]
  (select-box/create
   {:skin skin
    :items (map ->edn-str (rest schema))
    :selected (->edn-str v)}))
