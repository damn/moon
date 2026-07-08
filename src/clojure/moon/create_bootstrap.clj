(ns clojure.moon.create-bootstrap)

(defn f [^com.badlogic.gdx.Application app]
  {:ctx/audio (.getAudio app)
   :ctx/files (.getFiles app)
   :ctx/graphics (.getGraphics app)
   :ctx/input (.getInput app)
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
