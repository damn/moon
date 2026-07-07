(ns clojure.levelgen-test-change-listener
  (:require [clojure.utils-change-listener :as change-listener]
            [clojure.app-event :as app-event]))

(defn f [f]
  (change-listener/create
   (app-event/f f)))
