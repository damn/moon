(ns clojure.moon.update-mouse-positions
  (:require [clojure.mouse-position :refer [mouse-position]]
            [clojure.unproject :as unproject]))

(defn f
  [{:keys [ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [mp (mouse-position ctx)]
    (-> ctx
        (assoc :ctx/world-mouse-position (unproject/f world-viewport mp))
        (assoc :ctx/ui-mouse-position (-> stage :stage/viewport (unproject/f mp))))))
