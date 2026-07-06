(ns editor.create-widget.default
  (:require [clojure.string.truncate :refer [truncate]]
            [clojure.edn-str :refer [->edn-str]]
            [gdx.scene2d.ui.label :as label]))

(defn f
  [_ v {:keys [ctx/skin]}]
  (label/create
   {:text (truncate (->edn-str v) 60)
    :skin skin}))
