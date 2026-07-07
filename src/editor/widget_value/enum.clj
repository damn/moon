(ns editor.widget-value.enum
  (:require [clojure.select-box :as select-box]
            [clojure.edn :as edn]))

(defn f [_  widget _schemas]
  (edn/read-string (select-box/get-selected widget)))
