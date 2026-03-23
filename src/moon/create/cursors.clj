(ns moon.create.cursors
  (:import (com.badlogic.gdx Files
                             Graphics)
           (com.badlogic.gdx.graphics Pixmap))) ; TODO most interesting class - not an interface
; what the fuk is a Pixmap?
; What I ' do' with Pixmap class =?

(defn- create-cursor
  [{:keys [^Files ctx/files
           ctx/graphics]}
   path-format
   [path [hotspot-x hotspot-y]]]
  (let [pixmap (Pixmap. (.internal files (format path-format path))) ; TODO here just 'file-handle' simpler
        cursor (Graphics/.newCursor graphics pixmap hotspot-x hotspot-y)]
    (.dispose pixmap)
    cursor))

(defn do!
  [ctx {:keys [data path-format]}]
  ; TODO 1st step convert file-handles ???
  (assoc ctx :ctx/cursors (update-vals data (partial create-cursor ctx path-format))))

; TODO files stuff, graphics protocol?

; TODO ITS DIFFICULT TO TEST BECAUSE I DONT KNOW WHAT THOSE OBJECTS
; REQUIRE BECAUSE OF GDX GLOBAL STATE!!!

; SO INTEGRATE ALL GDX AND REMOVE GDX

; or I could just test my 'logic' by  binding pixmap to the libgdx plattform context ?? or graphics ?

; gdx/create-cursor gdx-context path [hotspot-x hotspot-y]
