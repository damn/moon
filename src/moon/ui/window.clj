(ns moon.ui.window
  (:require [moon.ui.actor :as actor]
            [gdl.ui.change-listener :as change-listener]
            [gdl.ui.stage :as stage]
            [gdl.ui.text-button :as text-button]
            [gdl.ui.window :as window]
            [gdl.ui.window :as window]
            [moon.ui.table :as table]))

; FIXME opts not there anymore
; TODO cannot close !
; TODO WASD in textfield -> player moves -> InputMultiplexer?
(defn- create*
  [{:keys [title
           close-button?
           center?
           close-on-escape?]}
   skin]
  (let [#_show-window-border? #_true
        window (window/create title skin)]
    #_(when close-button?    (.addCloseButton window))
    #_(when center?          (.centerWindow   window))
    #_(when close-on-escape? (.closeOnEscape  window))
    window))

(defn create
  [{:keys [skin modal?] :as opts}]
  (let [window (create* opts skin)]
    (table/add! (window/title-table window)
                (doto (text-button/create "X" skin)
                  (actor/add-listener!
                   (change-listener/create
                    (fn [_event _actor]
                      (actor/remove! window))))))
    (window/set-modal! window (boolean modal?))
    (table/set-opts! window opts)))

(defmethod stage/build :actor/window [opts]
  (create opts))

(defn find-ancestor
  "Finds the ancestor window of actor, otherwise throws an error if none of recursively searched parents of actors is a window actor."
  [actor]
  (window/find-ancestor actor))
