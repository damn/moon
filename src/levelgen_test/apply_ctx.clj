(ns levelgen-test.apply-ctx
  (:require [clojure.gdx.stage.set-ctx :as set-ctx]))

(defn f [stage f]
  (set-ctx/f stage (f (:stage/ctx stage))))
