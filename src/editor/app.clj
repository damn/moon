(ns editor.app
  (:require [clojure.core-ext :as core]))

(def state (atom nil))

(defn -main []
  (core/run-executions-from-edn! "config/editor.edn"))
