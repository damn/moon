(ns moon.ui.actor
  (:refer-clojure :exclude [name]))

; ** TODO this contains '2' things !!!
(defmulti create :type)

(defprotocol Actor
  (name [_])
  (x [_])
  (y [_])
  (width [_])
  (height [_])
  (user-object [_])
  (stage [_])
  (set-name! [_ name])
  (set-user-object! [_ object])
  (set-position! [_ x y align]
                 [_ [x y]])
  (set-visible! [_ visible?])
  (set-touchable! [_ touchable])
  (visible? [_])
  (hit [_ [x y] touchable?])
  (remove! [_])
  (parent [_])
  (stage->local-coordinates [actor xy])
  (add-listener! [actor [listener-k listener-params]])
  (find-ancestor [_ ui-type-k])
  (button? [_])
  (window-title-bar? [_])
  (toggle-visible! [_])
  (set-opts! [_ opts]))
