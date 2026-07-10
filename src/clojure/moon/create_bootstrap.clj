(ns clojure.moon.create-bootstrap
  (:require [gdl.application :as application]))

(defn f [application]
  {:ctx/audio    (application/get-audio    application)
   :ctx/files    (application/get-files    application)
   :ctx/graphics (application/get-graphics application)
   :ctx/input    (application/get-input    application)
   :ctx/unit-scale (atom 1)
   :ctx/active-entities nil
   :ctx/delta-time nil
   :ctx/ui-mouse-position nil
   :ctx/world-mouse-position nil
   :ctx/mouseover-eid nil
   :ctx/paused? false
   :ctx/elapsed-time 0
   :ctx/potential-field-cache (atom nil)
   :ctx/id-counter (atom 0)
   :ctx/entity-ids (atom {})
   :ctx/show-potential-field-colors? nil
   :ctx/show-cell-entities? false
   :ctx/show-cell-occupied? false
   :ctx/show-body-bounds? false})
