(ns clojure.editor.create-widget-default
  (:require [clojure.editor.create-widget :refer [create-widget]]
            [clojure.edn.v-to-str :refer [->edn-str]]
            [clojure.truncate :refer [truncate]]
            [clojure.ui-label :as label]))

(defmethod create-widget :default
  [_ v {:keys [ctx/skin]}]
  (label/create
   {:text (truncate (->edn-str v) 60)
    :skin skin}))
