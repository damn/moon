(ns levelgen-test.apply-ctx
  (:require [com.badlogic.gdx.scenes.scene2d.stage :as stage]))

(defn f [stage f]
  (stage/set-ctx! stage (f (:stage/ctx stage))))
